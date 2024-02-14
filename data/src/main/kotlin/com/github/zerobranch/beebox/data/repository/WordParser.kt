package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.commons_java.ext.capitalized
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Hint
import com.github.zerobranch.beebox.domain.models.words.Transcription
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.UUID

object WordParser {
    fun parse(word: String, body: String): DraftWord? {
        val content = Jsoup.parse(body)
            .getElementsByTag("body")
            .first()
            ?.getElementById("main_layout")
            ?.getElementById("container")
            ?.getElementById("content")
            ?: return null

        val wd = content.getElementById("wd") ?: return null
        val wdTitle = wd.getElementById("wd_title") ?: return null

        val translateInline = wd
            .getElementById("wd_content")
            ?.getElementById("content_in_russian")
            ?.getElementsByClass("t_inline_en")
            ?: return null

        val divsTranscriptions = wdTitle
            .getElementsByClass("trans_sound")
            .first()
            ?.children()
            ?: return null

        val rank = content
            .getElementById("word_rank_box")
            ?.text()
            ?.trim()
            ?.toIntOrNull() ?: -1

        val hints = parseHints(wdTitle)
        val transcriptions = parseTranscriptions(divsTranscriptions)
        val translates = translateInline.first()?.text()

        return DraftWord(
            word = word,
            translates = translates ?: "",
            rank = rank,
            transcriptions = transcriptions,
            hints = hints
        )
    }

    private fun parseHints(wdTitle: Element): List<Hint> {
        val wordForms = wdTitle.getElementById("word_forms")
        val spans = wordForms?.getElementsByTag("span") ?: return emptyList()
        val a = wordForms.getElementsByTag("a") ?: return emptyList()

        return if (a.isEmpty()) {
            listOf(
                Hint(
                    id = UUID.randomUUID().toString(),
                    text = wordForms.text()
                )
            )
        } else {
            List(spans.size) { i ->
                Hint(
                    id = UUID.randomUUID().toString(),
                    text = "${spans.getOrNull(i)?.text()} ${a.getOrNull(i)?.text()}"
                )
            }
        }
    }

    private fun parseTranscriptions(divsTranscriptions: Elements): List<Transcription> {
        val transcriptions = mutableListOf<Transcription>()
        var i = 0
        while (i < divsTranscriptions.size - 1) {
            val div = divsTranscriptions[i]

            val title = if (div.className().contains("es_div_")) {
                i++
                div.getElementsByClass(div.className())
                    .first()
                    ?.getElementsByClass("es_i")
                    ?.first()
                    ?.text()
            } else ""

            if (divsTranscriptions[i].id() == "us_tr_sound") {
                var items = divsTranscriptions[i]?.children() ?: return emptyList()
                val firstTranscriptionHint = items[0]?.text()?.trim() ?: return emptyList()
                val firstTranscription = items[1]?.text()?.trim() ?: return emptyList()
                val firstTranscriptionAudio = items.getOrNull(2)
                    ?.children()
                    ?.first()
                    ?.attributes()
                    ?.get("src")

                i++

                if (divsTranscriptions[i].id() == "uk_tr_sound") {
                    items = divsTranscriptions[i]?.children() ?: return emptyList()
                    val secondTranscriptionHint = items[0]?.text()?.trim() ?: return emptyList()
                    val secondTranscription = items[1]?.text()?.trim() ?: return emptyList()
                    val secondTranscriptionAudio = items.getOrNull(2)
                        ?.children()
                        ?.first()
                        ?.attributes()
                        ?.get("src")

                    transcriptions.add(
                        Transcription(
                            id = UUID.randomUUID().toString(),
                            title = title?.capitalized() ?: "",
                            firstTranscriptionHint = firstTranscriptionHint,
                            firstTranscription = firstTranscription,
                            firstTranscriptionAudio = firstTranscriptionAudio ?: "",
                            secondTranscriptionHint = secondTranscriptionHint,
                            secondTranscription = secondTranscription,
                            secondTranscriptionAudio = secondTranscriptionAudio ?: "",
                        )
                    )
                }
            } else {
                val hints = divsTranscriptions[i].getElementsByTag("i")
                val transcriptionsRaw = divsTranscriptions[i].getElementsByClass("transcription")
                val audios = divsTranscriptions[i].getElementsByTag("audio")

                val firstTranscriptionHint =
                    hints.getOrNull(0)?.text()?.trim() ?: return emptyList()
                val secondTranscriptionHint =
                    hints.getOrNull(1)?.text()?.trim() ?: return emptyList()
                val firstTranscription =
                    transcriptionsRaw.getOrNull(0)?.text()?.trim() ?: return emptyList()
                val secondTranscription =
                    transcriptionsRaw.getOrNull(1)?.text()?.trim() ?: return emptyList()

                val firstTranscriptionAudio = audios.getOrNull(0)
                    ?.children()
                    ?.first()
                    ?.attributes()
                    ?.get("src")

                val secondTranscriptionAudio = audios.getOrNull(1)
                    ?.children()
                    ?.first()
                    ?.attributes()
                    ?.get("src")

                transcriptions.add(
                    Transcription(
                        id = UUID.randomUUID().toString(),
                        title = title?.capitalized() ?: "",
                        firstTranscriptionHint = firstTranscriptionHint,
                        firstTranscription = firstTranscription.replace("|", ""),
                        firstTranscriptionAudio = firstTranscriptionAudio ?: "",
                        secondTranscriptionHint = secondTranscriptionHint,
                        secondTranscription = secondTranscription.replace("|", ""),
                        secondTranscriptionAudio = secondTranscriptionAudio ?: "",
                    )
                )
            }
            i++
        }

        return transcriptions
    }
}