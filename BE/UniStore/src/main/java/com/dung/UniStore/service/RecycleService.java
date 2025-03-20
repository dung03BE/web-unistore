package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.InventoryItemRequest;
import com.dung.UniStore.dto.request.RecycleRequest;
import com.dung.UniStore.dto.response.RecycleResponse;
import com.dung.UniStore.entity.InventoryItem;
import com.dung.UniStore.entity.Recycle;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.repository.IRecycleRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final IRecycleRepository recycleRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;
    public RecycleResponse createRecycleRequest(RecycleRequest request, Integer userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
       Recycle recycle = new Recycle();
       recycle.setDeviceType(request.getDeviceType());
       recycle.setDeviceCondition(request.getDeviceCondition());
       recycle.setCreatedAt(LocalDateTime.now());
       recycle.setPickupMethod(request.getPickupMethod());
       recycle.setStatus("Chờ xác nhận");
       recycle.setUser(user);
        recycle.setImageUrl(request.getImageUrl());
       recycleRepository.save(recycle);
       RecycleResponse response = modelMapper.map(recycle, RecycleResponse.class);
       response.setFullName(recycle.getUser().getFullName());
       response.setPhoneNumber(recycle.getUser().getPhoneNumber());
       return response;
    }

    public List<RecycleResponse> getAllRecycleRequest() {
        List<Recycle> recycles = recycleRepository.findAll();
        Type listType = new TypeToken<List<RecycleResponse>>() {}.getType();
        return modelMapper.map(recycles, listType);
    }
    public List<RecycleResponse> getAllRecycleRequest(Long userId) {
        List<Recycle> recycles = recycleRepository.findAllByUserId(userId);
        Type listType = new TypeToken<List<RecycleResponse>>() {}.getType();
        return modelMapper.map(recycles, listType);
    }


    public RecycleResponse updateStatusRecycle(int id, RecycleRequest request) throws ApiException {
        Recycle recycle = recycleRepository.findById(id).orElseThrow(
                ()-> new ApiException("Dont have recycle request with id: "+id)
        );
        recycle.setStatus(request.getStatus());
        recycleRepository.save(recycle);
        RecycleResponse response = modelMapper.map(recycle, RecycleResponse.class);
        response.setFullName(recycle.getUser().getFullName());
        response.setPhoneNumber(recycle.getUser().getPhoneNumber());
        return response;
    }
}
