package com.anwesh.uiprojects.stepdiagonallineview

/**
 * Created by anweshmishra on 04/02/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

val nodes : Int = 5
val lines : Int = 5
val scGap : Float = 0.02f / lines
val strokeFactor : Float = 90f
val delay : Long = 20
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawDiagonalLine(i : Int, w : Float, scale : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, lines)
    val sj : Float = 1f - 2 * i
    val si : Int = i % 2
    val gap : Float = w / lines
    save()
    translate(i * gap, -gap + gap * si)
    drawLine(0f, 0f, gap * sf, gap * sf * sj, paint)
    restore()
}

fun Canvas.drawDiagonalLines(w : Float, scale : Float, paint : Paint) {
    val scDiv : Double = 1.0 / lines
    val k : Int = Math.floor(scale.sinify() / scDiv).toInt()
    for (j in 0..k) {
        drawDiagonalLine(j, w, scale, paint)
    }
}

fun Canvas.drawSDLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    val gap : Float = h / (nodes + 1)
    save()
    translate(0f, gap * (i + 1))
    for (j in 0..(lines - 1)) {
        drawDiagonalLines(w, scale, paint)
    }
    restore()
}

class StepDiagonalLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SDLNode(var i : Int, val state : State = State()) {

        private var next : SDLNode? = null
        private var prev : SDLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SDLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSDLNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SDLNode {
            var curr : SDLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StepDiagonalLine(var i : Int) {

        private var curr : SDLNode = SDLNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StepDiagonalLineView) {

        private val animator : Animator = Animator(view)
        private val sdl : StepDiagonalLine = StepDiagonalLine(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sdl.draw(canvas, paint)
            animator.animate {
                sdl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sdl.update {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : StepDiagonalLineView {
            val view : StepDiagonalLineView = StepDiagonalLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}