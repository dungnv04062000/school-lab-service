package com.schoollab.dto.mapper;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenericMapper {

    @Autowired
    private ModelMapper modelMapper;

    public <T, E> E mapToType(T source, Class<E> typeDestination) {
        return modelMapper.map(source, typeDestination);
    }

    public <T, E> E mapToTypeNotNullProperty(T source, Class<E> typeDestination) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(source, typeDestination);
    }

    public <S, T> List<T> mapToListOfType(List<S> source, Class<T> targetClass) {
        return source.stream().map(item -> modelMapper.map(item, targetClass)).collect(Collectors.toList());
    }

    public <S, T> List<T> mapToListOfTypeNotNullProperty(List<S> source, Class<T> targetClass) {
        return source.stream().map(item -> mapToTypeNotNullProperty(item, targetClass)).collect(Collectors.toList());
    }

    public <T, E> void mapSrcToDestNotNullProperty(T source, E destination) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(source, destination);
    }

    public <T, E> void mapSrcToDest(T source, E target) {
        BeanUtils.copyProperties(source, target);
    }

    public <T, E> E toDto(T source, Class<E> typeDestination) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(source, typeDestination);
    }

    public <S, T> Page<T> toPage(Page<S> source, Class<T> targetClass, Pageable pageable) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Page<T> page = new PageImpl<T>(
                source.stream().map(item -> modelMapper.map(item, targetClass)).collect(Collectors.toList()), pageable,
                source.getTotalElements());
        return page;
    }

    public void copyPropertiesIgnoreNull(Object source, Object target) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        BeanUtils.copyProperties(source, target, emptyNames.toArray(result));
    }
}