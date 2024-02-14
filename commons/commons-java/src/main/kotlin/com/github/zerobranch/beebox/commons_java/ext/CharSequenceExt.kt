package com.github.zerobranch.beebox.commons_java.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun CharSequence?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }

    return this != null && this.isNotBlank()
}

@OptIn(ExperimentalContracts::class)
public inline fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }

    return this != null
}

@OptIn(ExperimentalContracts::class)
public inline fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }

    return this == null
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Collection<T>?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }

    return this == null
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }

    return this != null && this.isNotEmpty()
}
