package com.anwesh.uiprojects.linkedstepdiagonallineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.stepdiagonallineview.StepDiagonalLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StepDiagonalLineView.create(this)
    }
}
