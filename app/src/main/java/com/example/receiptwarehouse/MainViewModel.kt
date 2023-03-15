package com.example.receiptwarehouse

import androidx.lifecycle.ViewModel
import io.realm.Realm
import java.util.UUID

class MainViewModel : ViewModel() {

    private var realm = Realm.getDefaultInstance()

    fun addArticle(file: String, data: String) {

        realm.executeTransaction {
            var article = it.createObject(ArticleModel::class.java, UUID.randomUUID().toString())
            article.file = file
            article.data = data
            realm.insertOrUpdate(article)
        }

    }

}