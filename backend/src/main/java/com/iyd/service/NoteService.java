package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.NoteDTO;

public interface NoteService {
    R<?> createNote(Long userId, NoteDTO dto);
    R<?> updateNote(Long noteId, NoteDTO dto);
    R<?> deleteNote(Long noteId);
    R<?> getNoteList(Long userId, Long folderId);
    R<?> ocrExtract(String imageUrl);
    R<?> generateMindMap(Long noteId);
    R<?> createFolder(Long userId, String folderName, Long parentId);
    R<?> getFolders(Long userId);
}
