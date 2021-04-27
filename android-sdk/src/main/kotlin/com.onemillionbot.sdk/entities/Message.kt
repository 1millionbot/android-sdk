package com.onemillionbot.sdk.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Message {
    abstract val id: String
    abstract val botConfigColorHex: String

    sealed class Bot : Message() {
        data class PlaceholderWriting(
            override val id: String = ID,
            val message: String,
            override val botConfigColorHex: String
        ) : Bot() {
            companion object {
                const val ID = "PlaceholderWriting"
            }
        }

        data class Text(
            override val id: String,
            val message: String,
            override val botConfigColorHex: String
        ) : Bot()

        data class Image(
            override val id: String,
            val url: String,
            override val botConfigColorHex: String
        ) : Bot()

        data class Video(
            override val id: String,
            val url: String,
            val urlImage: String?,
            override val botConfigColorHex: String
        ) : Bot()

        data class ButtonsOverTextField(
            override val id: String,
            val buttons: List<MessageButton>,
            override val botConfigColorHex: String
        ) : Bot()

        @Parcelize
        data class CardCollection(
            override val id: String,
            val cards: List<Card>,
            override val botConfigColorHex: String
        ) : Bot(), Parcelable

        @Parcelize
        data class Card(
            val id: String,
            val title: String?,
            val subTitle: String?,
            val imageUrl: String?,
            val buttons: List<MessageButton>
        ) : Parcelable

        sealed class MessageButton : Parcelable {
            @Parcelize
            data class Text(val title: String, val value: String) : MessageButton()

            @Parcelize
            data class Link(val title: String, val link: String) : MessageButton()
        }
    }

    sealed class User : Message() {
        data class Text(
            override val id: String,
            val message: String,
            override val botConfigColorHex: String
        ) : User()
    }
}






