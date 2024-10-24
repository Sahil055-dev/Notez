package com.example.notez.bookmark

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notez.database.AppDatabase
import com.example.notez.database.BookmarkEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val bookmarkDao = AppDatabase.getDatabase(application).bookmarkDao()

    private val _bookmarks = MutableLiveData<List<BookmarkEntity>>()
    val bookmarks: LiveData<List<BookmarkEntity>> = _bookmarks

    init {
        viewModelScope.launch {
            _bookmarks.value = bookmarkDao.getAllBookmarks()
        }
    }

    fun addBookmark(noteUrl: String, fileName: String?, uploadDate: String?, userName: String?) {
        viewModelScope.launch {
            val bookmark = BookmarkEntity(noteUrl = noteUrl, fileName = fileName, uploadDate = uploadDate, userName = userName)
            bookmarkDao.insertBookmark(bookmark)
            // Update LiveData after adding the bookmark
            _bookmarks.value = bookmarkDao.getAllBookmarks()
        }
    }

    fun removeBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(bookmark)
            // Update LiveData after removing the bookmark
            _bookmarks.value = bookmarkDao.getAllBookmarks()
        }
    }
}








