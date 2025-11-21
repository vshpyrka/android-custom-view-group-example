package com.example.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.example.customviewgroup.R
import kotlin.math.max

class ContactListItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val preferredListItemHeight: Int
    val iconView: ImageFilterView
    private val photoViewWidth: Int
    private val photoViewHeight: Int

    val titleView: TextView
    val subtitleView: TextView
    private val backgroundColor = Color.BLUE

    init {
        val attributes = intArrayOf(android.R.attr.listPreferredItemHeight)
        val preferredHeightAttrs = context.obtainStyledAttributes(attributes)
        try {
            preferredListItemHeight =
                preferredHeightAttrs.getDimension(0, 64.0f.dpToPx(context)).toInt()
        } finally {
            preferredHeightAttrs.recycle()
        }

        foreground = ContextCompat.getDrawable(context,
            with(TypedValue()) {
                context.theme.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    this,
                    true
                )
                resourceId
            }
        )

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ContactListItem,
            R.attr.contactListItemStyle,
            R.style.Widget_AppTheme_ContactListItem
        )
        photoViewWidth = typedArray.getLayoutDimension(
            R.styleable.ContactListItem_avatar_width, LayoutParams.WRAP_CONTENT
        )
        photoViewHeight = typedArray.getLayoutDimension(
            R.styleable.ContactListItem_avatar_height, LayoutParams.WRAP_CONTENT
        )
        typedArray.recycle()

        iconView = ImageFilterView(context)
            .apply {
                roundPercent = 1f
                scaleType = ImageView.ScaleType.FIT_XY
            }
            .also(::addView)

        titleView = TextView(context).apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            TextViewCompat.setTextAppearance(this, android.R.style.TextAppearance_Large)
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(Color.DKGRAY)
        }.also {
            val params = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            params.marginStart = 16.0f.dpToPx(context).toInt()
            addView(it, params)
        }

        subtitleView = TextView(context).apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            TextViewCompat.setTextAppearance(this, android.R.style.TextAppearance_Small)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.LTGRAY)
        }.also {
            val params = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            params.marginStart = 16.0f.dpToPx(context).toInt()
            addView(it, params)
        }

        displayEditorData()
    }

    @SuppressLint("SetTextI18n")
    private fun displayEditorData() {
        if (isInEditMode) {
            iconView.setImageResource(R.drawable.ic_cat)
            iconView.background = ColorDrawable(Color.CYAN)
            titleView.text = "Hello"
            titleView.background = ColorDrawable(Color.BLUE)
            subtitleView.text = "World"
            subtitleView.background = ColorDrawable(Color.MAGENTA)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /*
            1) Measure Spec + Padding There is two methods that will take a width MeasureSpec
            from the parent, take a height MeasureSpec from the parent, and take a view,
            read its layout parameters, and figure out, based on that view group, that view
            group’s own constraints, and take that into account, but still give that child
            those layout parameters to the best of its ability, and it is called measureChild(...).
            There is measureChildren(...), which takes measure child and calls it on all of the
            children in that view group. BUT, If you like margins, these first two methods do not
            take margins into account.

            2) Measure Spec + Padding + Margins Much of that has to do with the fact that margins
            are not handled by default. There is this method called measureChildWithMargins(...),
            it has met a child, but in that calculation and reconciling of what the parent can be
            versus what the children want to be, it will take the margins into account.
            It will pull out the margins from that child and add them into the calculations.

            3) Measure Spec + Child Layout Parameters You might not even need to use this method,
            but is handy in case you want to do your own stuff: getChildMeasureSpec(...).
            All that complicated logic about reconciling how big the parent can be versus
            how big the children want to be, that is inside of getChildMeasureSpec(...).
            It does the hard work of figuring out a MeasureSpec to give those children, based on
            what the view group can be and what the children want to be. That is called within
            measure child and measure child with params, but if you want to end up doing your own,
            you can call on that method.
         */

        // Measure icon.
        measureChildWithMargins(
            iconView,
            widthMeasureSpec, 0,
            heightMeasureSpec, 0
        )

        // Figure out how much width the icon used.
        val photoViewLayoutParams = iconView.layoutParams as MarginLayoutParams
        val widthUsed =
            photoViewWidth + photoViewLayoutParams.marginStart + photoViewLayoutParams.marginEnd

        val heightUsed = 0

        // Measure title
        measureChildWithMargins(
            titleView,
            // Pass width constraints and width already used.
            widthMeasureSpec, widthUsed,
            // Pass height constraints and height already used.
            heightMeasureSpec, heightUsed
        )

        // Measure the Subtitle.
        measureChildWithMargins(
            subtitleView,
            // Pass width constraints and width already used.
            widthMeasureSpec, widthUsed,
            // Pass height constraints and height already used.
            heightMeasureSpec, titleView.measuredHeight
        )

        // Calculate this view's measured width and height.

        // Figure out how much total space the icon used.
        val iconWidth =
            photoViewWidth + photoViewLayoutParams.marginStart + photoViewLayoutParams.marginEnd
        val iconHeight =
            photoViewHeight + photoViewLayoutParams.topMargin + photoViewLayoutParams.bottomMargin

        // Figure out how much total space the title used.
        val titleLayoutParams = titleView.layoutParams as MarginLayoutParams
        val titleWidth =
            titleView.measuredWidth + titleLayoutParams.marginStart + titleLayoutParams.marginEnd
        val titleHeight =
            titleView.measuredHeight + titleLayoutParams.topMargin + titleLayoutParams.bottomMargin

        // Figure out how much total space the subtitle used.
        val subtitleLayoutParams = subtitleView.layoutParams as MarginLayoutParams
        val subtitleWidth =
            subtitleView.measuredWidth + subtitleLayoutParams.marginStart + subtitleLayoutParams.marginEnd
        val subtitleHeight =
            subtitleView.measuredHeight + subtitleLayoutParams.topMargin + subtitleLayoutParams.bottomMargin

        // The width taken by the children + padding.
        val width = paddingStart + paddingEnd + iconWidth + max(titleWidth, subtitleWidth)
        val height = max(
            paddingTop + paddingBottom + max(iconHeight, titleHeight + subtitleHeight),
            preferredListItemHeight
        )

        // Reconcile the measured dimensions with the this view's constraints and
        // set the final measured width and height.
        setMeasuredDimension(
            resolveSize(width, widthMeasureSpec),
            resolveSize(height, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val iconLayoutParams = iconView.layoutParams as MarginLayoutParams
        val iconStartX = paddingStart + iconLayoutParams.marginStart
        val iconStartY = paddingTop + iconLayoutParams.topMargin

        // Layout the icon.
        iconView.layout(
            iconStartX,
            iconStartY,
            iconStartX + photoViewWidth,
            iconStartY + photoViewHeight
        )

        val titleLayoutParams = titleView.layoutParams as MarginLayoutParams
        val titleStartX =
            iconStartX + photoViewWidth + iconLayoutParams.marginEnd + titleLayoutParams.marginStart
        val titleStartY = titleLayoutParams.topMargin

        titleView.layout(
            titleStartX,
            titleStartY,
            titleStartX + titleView.measuredWidth,
            titleStartY + titleView.measuredHeight
        )

        val subtitleLayoutParams = subtitleView.layoutParams as MarginLayoutParams
        val subtitleStartX =
            iconStartX + photoViewWidth + iconLayoutParams.marginEnd + subtitleLayoutParams.marginStart
        val subtitleStartY =
            titleLayoutParams.topMargin + titleView.measuredHeight + subtitleLayoutParams.topMargin
        subtitleView.layout(
            subtitleStartX,
            subtitleStartY,
            subtitleStartX + subtitleView.measuredWidth,
            subtitleStartY + subtitleView.measuredHeight
        )
    }

    /**
     * checkLayoutParams(), a validation method, it checks whether a particular
     * ViewGroup.LayoutParams distance is valid to use with that ViewGroup.
     * If I stuck a relative layout, specific layout params, in a linear layout,
     * checkLayoutParams() would say, “that is not what we use around here.”
     * There are three methods that generate layout params.
     *
     * Validates if a set of layout parameters is valid for a child this ViewGroup.
     */
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    /**
     * generateDefaultLayoutParams() is called when ViewGroup gets a view that has no
     * layout parameters on it, it can have some default layout parameters.
     *
     * @return A set of default layout parameters when given a child with no layout parameters.
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    /**
     * generateDefaultLayoutParams(ViewGroup.LayoutParams p) - takes ViewGroup.LayoutParams as
     * a parameter. This is used when a view gets a bad layout parameter
     * (e.g. pass some relative layout parameters into a linear layout). This method will give
     * you a chance to take that layout parameter (e.g. that invalid layout parameter object),
     * and pull out stuff that you can use. You can pull out the width the height and any
     * other properties that you can use in your specific layout parameter’s object,
     * and you create a new, valid version using some of those properties from the bad one.
     *
     * @return A set of layout parameters created from attributes passed in XML.
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    /**
     * generateDefaultLayoutParams(AttributeSet attrs), a generate layout params that takes an
     * attribute set. AttributeSet is our friend from XML inflation, and it is generating a set of
     * layout params based on attributes specified in XML.
     *
     * Called when {@link #checkLayoutParams(LayoutParams)} fails.
     *
     * @return A set of valid layout parameters for this ViewGroup that copies appropriate/valid
     * attributes from the supplied, not-so-good-parameters.
     */
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return generateDefaultLayoutParams()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        super.dispatchDraw(canvas)
    }

    private fun Float.dpToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
        )
    }
}
