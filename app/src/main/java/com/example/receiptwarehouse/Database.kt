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
    var imageUrl: String = ""
    constructor(f: String, b: RealmList<BlockItem>, im: String) : this() {
        filename = f
        blocks = b
        imageUrl = im
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

    fun write(f: String, b: RealmList<BlockItem>, im: String) {
        val extractedItem = ExtractedItem(f, b, im)
        realm.writeBlocking {
            copyToRealm(extractedItem)
        }
    }

    fun getAll(): ArrayList<ReceiptDataClass> {
        val all: RealmResults<ExtractedItem> = realm.query<ExtractedItem>().find()
        val paths = all.map { it.imageUrl }
        val blocksList = all.map { it.blocks }

        val texts = mutableListOf<String>()

        for (blocks in blocksList) {
            var text = ""
            for (block in blocks) {
                for (line in block.lines) {
                    for (word in line.words) {
                        text += word + "\n"
                    }
                }
            }
            texts.add(text)
        }

        val ret: ArrayList<ReceiptDataClass> = ArrayList()

        for (i in 0 until all.size) {
            val temp = ReceiptDataClass(paths[i], texts[i])
            ret.add(temp)
        }

        return ret
    }

}