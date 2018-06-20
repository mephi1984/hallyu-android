package com.fishrungames.hallyu.models

import java.io.Serializable

class Post : Serializable {

    var id: Int? = null

    var feedId: Int? = null

    var header: String? = null

    var text: String? = null

    var commentMode: Int? = null

    var username: String? = null

    var date: String? = null

    var commentsCount: Int? = null

    var images: List<Image>? = null

    var newComment: Boolean = false

}
