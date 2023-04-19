package com.optiapk.optiski

import com.optiapk.optiski.models.Piste

data class Station(val title: String, val coords: List<Double>, val map: String, val pistes: List<Piste>)