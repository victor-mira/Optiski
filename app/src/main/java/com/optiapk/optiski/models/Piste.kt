package com.optiapk.optiski.models

data class Piste (val start_lift : Int, var number: String, val difficulty: Int,
                  val distance : Int, val time: List<Int>, val end_lift : List<Int>)