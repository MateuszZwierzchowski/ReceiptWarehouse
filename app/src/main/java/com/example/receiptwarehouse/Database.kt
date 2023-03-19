package com.example.receiptwarehouse

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class ExtractedItem() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var filename: String = ""
    var blocks: RealmList<BlockItem> = realmListOf()

    constructor(f: String, b: RealmList<BlockItem>) : this() {
        filename = f
        blocks = b
    }
}


class BlockItem() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var lines: RealmList<LineItem> = realmListOf()

    constructor(l: RealmList<LineItem>) : this() {
        lines = l
    }
}


class LineItem() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var words: RealmList<String> = realmListOf()

    constructor(w: RealmList<String>) : this() {
        words = w
    }
}

object Database {
    private val config = RealmConfiguration.create(schema = setOf(ExtractedItem::class, BlockItem::class, LineItem::class))
    private val realm: Realm = Realm.open(config)

    fun write(f: String, b: RealmList<BlockItem>) {
        val extractedItem = ExtractedItem(f, b)
        realm.writeBlocking {
            copyToRealm(extractedItem)
        }
    }

    fun query() {
        val all: RealmResults<ExtractedItem> = realm.query<ExtractedItem>().find()

        Log.i("FOUND", all.get(1).blocks.get(0).lines.get(0).words.toString())
    }

}