package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.ResourceQueryDTO;

public interface ResourceService {
    R<?> searchResources(ResourceQueryDTO dto);
    R<?> getResourceDetail(Long resourceId);
    R<?> downloadResource(Long resourceId);
    R<?> collectResource(Long userId, Long resourceId);
    R<?> getMyCollections(Long userId);
}