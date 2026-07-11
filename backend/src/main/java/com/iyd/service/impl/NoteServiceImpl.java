package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.dto.NoteDTO;
import com.iyd.entity.AiGenerateRecord;
import com.iyd.entity.NoteFolder;
import com.iyd.entity.UserNote;
import com.iyd.mapper.AiGenerateRecordMapper;
import com.iyd.mapper.NoteFolderMapper;
import com.iyd.mapper.UserNoteMapper;
import com.iyd.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final UserNoteMapper noteMapper;
    private final NoteFolderMapper folderMapper;
    private final AiGenerateRecordMapper aiRecordMapper;

    @Override
    public R<?> createNote(Long userId, NoteDTO dto) {
        UserNote note = new UserNote();
        note.setUserId(userId);
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setOcrImage(dto.getOcrImage());
        note.setFolderId(dto.getFolderId());
        note.setNoteType(dto.getNoteType() != null ? dto.getNoteType() : 1);
        noteMapper.insert(note);
        return R.ok(note);
    }

    @Override
    public R<?> updateNote(Long noteId, NoteDTO dto) {
        UserNote note = noteMapper.selectById(noteId);
        if (note == null) return R.fail("笔记不存在");
        if (dto.getTitle() != null) note.setTitle(dto.getTitle());
        if (dto.getContent() != null) note.setContent(dto.getContent());
        noteMapper.updateById(note);
        return R.ok();
    }

    @Override
    public R<?> deleteNote(Long noteId) {
        noteMapper.deleteById(noteId);
        return R.ok();
    }

    @Override
    public R<?> getNoteList(Long userId, Long folderId) {
        LambdaQueryWrapper<UserNote> qw = new LambdaQueryWrapper<>();
        qw.eq(UserNote::getUserId, userId);
        if (folderId != null && folderId > 0) qw.eq(UserNote::getFolderId, folderId);
        qw.orderByDesc(UserNote::getUpdateTime);
        return R.ok(noteMapper.selectList(qw));
    }

    @Override
    public R<?> ocrExtract(String imageUrl) {
        // Simulate OCR extraction
        Map<String, Object> result = new HashMap<>();
        result.put("text", "OCR识别提取的文本内容（实际调用OCR API）。\n包含学生课堂笔记、知识点总结等手写内容。");
        result.put("confidence", 95.5);
        return R.ok(result);
    }

    @Override
    public R<?> generateMindMap(Long noteId) {
        UserNote note = noteMapper.selectById(noteId);
        if (note == null) return R.fail("笔记不存在");

        // Simulate AI mind map generation
        String mindMapData = "{\"root\":\"核心主题\",\"children\":[{\"name\":\"子主题1\",\"children\":[\"要点A\",\"要点B\"]},{\"name\":\"子主题2\",\"children\":[\"要点C\"]}]}";

        note.setMindMapImage("/oss/mindmap/" + noteId + ".png");
        noteMapper.updateById(note);

        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(note.getUserId());
        record.setGenerateType(3);
        record.setInputContent(note.getContent());
        record.setOutputContent(mindMapData);
        record.setOutputImage(note.getMindMapImage());
        aiRecordMapper.insert(record);

        Map<String, Object> result = new HashMap<>();
        result.put("mindMapImage", note.getMindMapImage());
        result.put("mindMapData", mindMapData);
        return R.ok(result);
    }

    @Override
    public R<?> createFolder(Long userId, String folderName, Long parentId) {
        NoteFolder folder = new NoteFolder();
        folder.setUserId(userId);
        folder.setFolderName(folderName);
        folder.setParentId(parentId != null ? parentId : 0L);
        folderMapper.insert(folder);
        return R.ok(folder);
    }

    @Override
    public R<?> getFolders(Long userId) {
        LambdaQueryWrapper<NoteFolder> qw = new LambdaQueryWrapper<>();
        qw.eq(NoteFolder::getUserId, userId).orderByAsc(NoteFolder::getSortOrder);
        return R.ok(folderMapper.selectList(qw));
    }
}
