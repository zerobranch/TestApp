package com.github.zerobranch.beebox.logging

import timber.log.Timber

fun <T> Class<T>.info(tag: String, msg: String) {
    Timber.tag(tag).i("[${this.simpleName}] $msg")
}

fun <T> Class<T>.warning(tag: String, th: Throwable?, msg: String) {
    Timber.tag(tag).w(th, "[${this.simpleName}] $msg")
}

fun <T> Class<T>.warning(tag: String, msg: String) {
    Timber.tag(tag).w("[${this.simpleName}] $msg")
}

fun <T> Class<T>.debug(tag: String, msg: String) {
    Timber.tag(tag).d("[${this.simpleName}] $msg")
}

fun <T> Class<T>.error(tag: String, th: Throwable, msg: String) {
    Timber.tag(tag).e(th, "[${this.simpleName}] $msg")
}

fun <T> Class<T>.error(tag: String, msg: String) {
    Timber.tag(tag).e("[${this.simpleName}] $msg")
}

fun <T> Class<T>.wtf(tag: String, msg: String) {
    Timber.tag(tag).wtf("[${this.simpleName}] $msg")
}

fun <T> Class<T>.wtf(tag: String, th: Throwable?, msg: String? = "") {
    Timber.tag(tag).wtf(th, "[${this.simpleName}] $msg")
}