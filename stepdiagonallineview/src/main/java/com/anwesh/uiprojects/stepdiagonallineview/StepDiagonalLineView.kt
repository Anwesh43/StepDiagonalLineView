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

fun Canvas.drawDLNode(i : Int, scale : Float, paint : Paint) {
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

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}