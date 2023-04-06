package com.github.danieldaeschle.ministrynotes.lib

import com.github.danieldaeschle.ministrynotes.data.Entry
import com.github.danieldaeschle.ministrynotes.data.EntryKind


fun List<Entry>.timeSum(): Time {
    var hours = this.sumOf { it.hours }
    var minutes = this.sumOf { it.minutes }
    hours += minutes / 60
    minutes %= 60
    return Time(hours, minutes)
}

fun List<Entry>.ministryTimeSum() = this.ministries().timeSum()

fun List<Entry>.theocraticAssignments() = this.filter { it.kind == EntryKind.TheocraticAssignment }

fun List<Entry>.theocraticAssignmentTimeSum() = this.theocraticAssignments().timeSum()

fun List<Entry>.theocraticSchools() = this.filter { it.kind == EntryKind.TheocraticSchool }

fun List<Entry>.theocraticSchoolTimeSum() = this.theocraticSchools().timeSum()

fun List<Entry>.ministries() =
    this.filter { it.kind in arrayOf(EntryKind.Ministry, EntryKind.Transfer) }

fun List<Entry>.transfers() = this.filter { it.kind == EntryKind.Transfer }

fun List<Entry>.placements() = this.ministries().sumOf { it.placements }

fun List<Entry>.returnVisits() = this.ministries().sumOf { it.returnVisits }

fun List<Entry>.videoShowings() = this.ministries().sumOf { it.videoShowings }