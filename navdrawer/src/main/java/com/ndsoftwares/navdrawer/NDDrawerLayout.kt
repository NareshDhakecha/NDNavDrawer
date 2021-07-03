package com.ndsoftwares.navdrawer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.round

open class NDDrawerLayout  : DrawerLayout {

    var settings = HashMap<Int, Setting?>()
    private var defaultScrimColor = -0x67000000
    private var defaultDrawerElevation = 0f
    private var frameLayout: FrameLayout? = null
    var drawerView: View? = null
    private var statusBarColor = 0
    private var defaultFitsSystemWindows = false
    private var contrastThreshold = 3f
    private var cardBackgroundColor = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NDDrawerLayout)
        cardBackgroundColor = a.getColor(R.styleable.NDDrawerLayout_ndl_cardBackgroundColor, 0)
        statusBarColor = a.getColor(R.styleable.NDDrawerLayout_ndl_statusBarColor, 0)
        a.recycle()
        defaultDrawerElevation = drawerElevation
        defaultFitsSystemWindows = fitsSystemWindows
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) if (!isInEditMode) {
//            statusBarColor = activity!!.window.statusBarColor
//        }
        addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                this@NDDrawerLayout.drawerView = drawerView
                updateSlideOffset(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
        frameLayout = FrameLayout(context)
        frameLayout!!.setPadding(0, 0, 0, 0)
        super.addView(frameLayout)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        child.layoutParams = params
        addView(child)
    }

    override fun addView(child: View) {
        if (child is NavigationView) {
            super.addView(child)
        } else {
            val cardView = CardView(context)
            cardView.radius = 0f
            cardView.addView(child)
            cardView.cardElevation = 0f
            cardView.setCardBackgroundColor(cardBackgroundColor)
            frameLayout!!.addView(cardView)
        }
    }

    fun setViewScale(gravity: Int, percentage: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else setting = settings[absGravity]
        setting?.percentage = percentage
        if (percentage < 1) {
            setStatusBarBackground(null)
            systemUiVisibility = 0
        }
        setting?.scrimColor = Color.TRANSPARENT
        setting?.drawerElevation = 0f
    }

    fun setViewElevation(gravity: Int, elevation: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else setting = settings[absGravity]
        setting?.scrimColor = Color.TRANSPARENT
        setting?.drawerElevation = 0f
        setting?.elevation = elevation
    }

    fun setViewScrimColor(gravity: Int, scrimColor: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else setting = settings[absGravity]
        setting?.scrimColor = scrimColor
    }

    fun setCardBackgroundColor(gravity: Int, color: Int) {
        cardBackgroundColor = color
        for (i in 0 until frameLayout!!.childCount) {
            val child = frameLayout!!.getChildAt(i) as CardView
            child.setCardBackgroundColor(cardBackgroundColor)
        }
    }

    fun setRadius(gravity: Int, radius: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else setting = settings[absGravity]
        setting!!.radius = radius
    }

    fun setViewRotation(gravity: Int, degree: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting() as Setting
            settings[absGravity] = setting
        } else setting = settings[absGravity] as Setting?


        setting?.degree = if (degree > 45) 45f else degree
        setting?.scrimColor = Color.TRANSPARENT
        setting?.drawerElevation = 0f
    }

    fun getSetting(gravity: Int): Setting? {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        return settings[absGravity]
    }

    override fun setDrawerElevation(elevation: Float) {
        defaultDrawerElevation = elevation
        super.setDrawerElevation(elevation)
    }

    override fun setScrimColor(@ColorInt color: Int) {
        defaultScrimColor = color
        super.setScrimColor(color)
    }

    fun useCustomBehavior(gravity: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        if (!settings.containsKey(absGravity)) {
            val setting = createSetting()
            settings[absGravity] = setting
        }
    }

    fun removeCustomBehavior(gravity: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        if (settings.containsKey(absGravity)) {
            settings.remove(absGravity)
        }
    }

    override fun openDrawer(drawerView: View, animate: Boolean) {
        super.openDrawer(drawerView, animate)
        post { updateSlideOffset(drawerView, if (isDrawerOpen(drawerView)) 1f else 0f) }
    }

    private fun updateSlideOffset(drawerView: View, slideOffset: Float) {
        val absHorizGravity = getDrawerViewAbsoluteGravity(GravityCompat.START)
        val childAbsGravity = getDrawerViewAbsoluteGravity(drawerView)
        val activity = activity
        val window = activity!!.window
        var isRtl = false
        isRtl = layoutDirection == View.LAYOUT_DIRECTION_RTL
//                    || window.decorView.layoutDirection == View.LAYOUT_DIRECTION_RTL || resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        for (i in 0 until frameLayout!!.childCount) {
            val child = frameLayout!!.getChildAt(i) as CardView
            val setting = settings[childAbsGravity]
            var adjust: Float
            if (setting != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setting.percentage < 1.0) {
                    if (drawerView.background is ColorDrawable) {
                        val color = ColorUtils.setAlphaComponent(statusBarColor, (255 - 255 * slideOffset).toInt())
                        window.statusBarColor = color
                        val bgColor = (drawerView.background as ColorDrawable).color
                        window.decorView.setBackgroundColor(bgColor)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            systemUiVisibility = if (ColorUtils.calculateContrast(Color.WHITE, bgColor) < contrastThreshold && slideOffset > 0.4) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                        }
                    } else if (drawerView.background is MaterialShapeDrawable
                        && (drawerView.background as MaterialShapeDrawable).fillColor != null) {
                        val color = ColorUtils.setAlphaComponent(statusBarColor, (255 - 255 * slideOffset).toInt())
                        window.statusBarColor = color
                        val bgColor = (drawerView.background as MaterialShapeDrawable).fillColor!!.defaultColor
                        window.decorView.setBackgroundColor(bgColor)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            systemUiVisibility = if (ColorUtils.calculateContrast(Color.WHITE, bgColor) < contrastThreshold && slideOffset > 0.4) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                        }
                    }
                }
                child.radius = (round((setting.radius * slideOffset) * 2.0) / 2).toFloat()
                super.setScrimColor(setting.scrimColor)
                super.setDrawerElevation(setting.drawerElevation)
                val percentage = 1f - setting.percentage
                ViewCompat.setScaleY(child, 1f - percentage * slideOffset)
                child.cardElevation = setting.elevation * slideOffset
                adjust = setting.elevation
                var isLeftDrawer: Boolean = if (isRtl) childAbsGravity != absHorizGravity else childAbsGravity == absHorizGravity
                val width = if (isLeftDrawer) drawerView.width + adjust else -drawerView.width - adjust
                updateSlideOffset(child, setting, width, slideOffset, isLeftDrawer)
            } else {
                super.setScrimColor(defaultScrimColor)
                super.setDrawerElevation(defaultDrawerElevation)
            }
        }
    }

    fun setContrastThreshold(contrastThreshold: Float) {
        this.contrastThreshold = contrastThreshold
    }

    open fun updateSlideOffset(child: CardView, setting: Setting, width: Float, slideOffset: Float, isLeftDrawer: Boolean) {
        if (setting.degree > 0) {
            val percentage = setting.degree / 90f
            child.x = width * slideOffset - child.width / 2.0f * percentage * slideOffset
            child.rotationY = (if (isLeftDrawer) -1 else 1) * setting.degree * slideOffset
        }
        else
            child.x = width * slideOffset
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (drawerView != null) updateSlideOffset(drawerView!!, if (isDrawerOpen(drawerView!!)) 1f else 0f)
    }

    fun getDrawerViewAbsoluteGravity(gravity: Int): Int {
        return GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this)) and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
    }

    fun getDrawerViewAbsoluteGravity(drawerView: View): Int {
        val gravity = (drawerView.layoutParams as LayoutParams).gravity
        return getDrawerViewAbsoluteGravity(gravity)
    }


    val activity: Activity?
        get() = getActivity(context)

    fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        return if (context is ContextWrapper) getActivity(context.baseContext) else null
    }

    open fun createSetting(): Setting? {
        return Setting()
    }

    open inner class Setting {
        var fitsSystemWindows = false
        var percentage = 1f
        var scrimColor = defaultScrimColor
        var elevation = 0f
        var drawerElevation = defaultDrawerElevation
        var radius = 0f
        var degree = 0f

    }

    companion object {
        private val TAG = NDDrawerLayout::class.java.simpleName
    }

}