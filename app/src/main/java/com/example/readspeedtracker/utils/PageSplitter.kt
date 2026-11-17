package com.example.readspeedtracker.utils

object PageSplitter {
    /**
     * Разбивает текст на страницы примерно одинакового размера (pageCharLimit),
     * не разрывая предложения посередине.
     */
    fun splitIntoPages(content: String, pageCharLimit: Int = 1000): List<String> {
        if (content.isEmpty()) return emptyList()

        // Разбиваем текст на предложения (точка, !, ? + пробел/перенос)
        val sentences = content.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val resultPages = mutableListOf<String>()
        val currentPageBuilder = StringBuilder()

        for (sentence in sentences) {
            if (currentPageBuilder.isNotEmpty() && currentPageBuilder.length + sentence.length > pageCharLimit) {
                resultPages.add(currentPageBuilder.toString().trim())
                currentPageBuilder.clear()
            }

            if (currentPageBuilder.isNotEmpty()) {
                currentPageBuilder.append(" ")
            }
            currentPageBuilder.append(sentence)
        }

        if (currentPageBuilder.isNotEmpty()) {
            resultPages.add(currentPageBuilder.toString().trim())
        }

        return resultPages.ifEmpty { listOf(content) }
    }

    /** Считает количество слов в строке (по пробелам). */
    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split(Regex("\\s+")).size
    }
}