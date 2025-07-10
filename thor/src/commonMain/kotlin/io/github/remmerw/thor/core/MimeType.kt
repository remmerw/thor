package io.github.remmerw.thor.core

object MimeType {
    /**
     * MIME type for PDF files.
     */
    const val PDF: String = "application/pdf"

    /**
     * MIME type for generic binary data.
     */
    const val OCTET_STREAM: String = "application/octet-stream"

    /**
     * MIME type for JSON data.
     */
    const val JSON: String = "application/json"

    /**
     * MIME type for plain text files.
     */
    const val PLAIN_TEXT: String = "text/plain"

    /**
     * MIME type for BitTorrent files.
     */
    const val TORRENT: String = "application/x-bittorrent"

    /**
     * MIME type for CSV files.
     */
    const val CSV: String = "text/csv"

    /**
     * MIME type for PGP public key files.
     */
    const val PGP_KEYS: String = "application/pgp-keys"

    /**
     * MIME type for Microsoft Excel 97-2003 files (.xls).
     */
    const val EXCEL: String = "application/vnd.ms-excel" // More specific than "application/msexcel"

    /**
     * MIME type for Microsoft Excel Open XML files (.xlsx).
     */
    const val OPEN_EXCEL: String =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    /**
     * MIME type for Microsoft Word 97-2003 files (.doc).
     */
    const val WORD: String = "application/msword"

    /**
     * MIME type for a directory (used by Android's Document Provider).
     */
    const val DIRECTORY: String = "vnd.android.document/directory"

    /**
     * MIME type for HTML files
     */
    const val HTML: String = "text/html"

    /**
     * MIME type for MHT files (web archive)
     */
    const val MHT: String = "multipart/related"

    // End region

    // Region: General Media Types
    /**
     * General MIME type for audio files.
     */
    const val AUDIO: String = "audio/*"

    /**
     * General MIME type for video files.
     */
    const val VIDEO: String = "video/*"

    /**
     * General MIME type for text files.
     */
    const val TEXT: String = "text/*"

    /**
     * General MIME type for application-specific files.
     */
    const val APPLICATION: String = "application/*"

    /**
     * General MIME type for image files.
     */
    const val IMAGE: String = "image/*"

    /**
     * General MIME type for any type of file.
     */
    const val ALL: String = "*/*"

}