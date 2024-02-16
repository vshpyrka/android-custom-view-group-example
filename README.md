# android-custom-view-group-example

This is an illustration of an Android custom view that generates and places views in their respective positions. The custom view includes unique attributes to demonstrate setting avatar size through layout attributes. 

<img width="300" alt="Screenshot 2024-02-16 at 8 23 58 PM" src="https://github.com/vshpyrka/android-custom-view-group-example/assets/2741602/8162134c-e1f1-496a-a159-10d49447dc7b">

In the onMeasure() method, the view implements logic to measure its bounds, accounting for dimensions such as margins and paddings of its children. 

```
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
  // Measure icon.
  measureChildWithMargins(
      iconView,
      widthMeasureSpec, 0,
      heightMeasureSpec, 0
  )
  ...

// Reconcile the measured dimensions with the this view's constraints and
  // set the final measured width and height.
  setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(height, heightMeasureSpec)
  )
```

Additionally, it employs custom logic in the onLayout() method to position the children within the measured bounds.

```
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
```
