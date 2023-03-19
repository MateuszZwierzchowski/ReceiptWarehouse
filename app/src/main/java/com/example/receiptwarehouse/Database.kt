package com.example.receiptwarehouse

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class ExtractedItem() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var filename: String = ""
    var extracted: String = ""

    constructor(f: String, e: String) : this() {
        filename = f
        extracted = e
    }
}

object Database {
    val config = RealmConfiguration.create(schema = setOf(ExtractedItem::class))
    val realm: Realm = Realm.open(config)

    fun write(f: String, e: String) {
        val extractedItem = ExtractedItem(f, e)
        realm.writeBlocking {
            copyToRealm(extractedItem)
        }
    }

    fun query() {
        val all: RealmResults<ExtractedItem> = realm.query<ExtractedItem>().find()
    }
}