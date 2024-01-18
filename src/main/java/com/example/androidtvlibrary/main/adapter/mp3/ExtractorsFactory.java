package com.example.androidtvlibrary.main.adapter.mp3;

import com.example.androidtvlibrary.main.adapter.Media.Extractor;

public interface ExtractorsFactory {

    /** Returns an array of new {@link Extractor} instances. */
    Extractor[] createExtractors();
}
