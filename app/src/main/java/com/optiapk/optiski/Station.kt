package com.optiapk.optiski

import com.optiapk.optiski.models.Piste

data class Station(val title: String, val coords: List<Double>, val pistes: List<Piste>, val lifts : List<Lift>)