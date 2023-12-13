package com.schoollab.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoollab.dto.PagingDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseBodyDto<E> {
    /*
     * =============================================================================
     * =================================== ===== PRIVATE PROPERTIES =====
     * =============================================================================
     * ===================================
     */

    /**
     * Reset API: Response code
     */
    private ResponseCode code;

    /**
     * Reset API: Response message
     */
    private String message;

    /**
     * Reset API: Response total items for pagination
     */

    @JsonProperty("total_items")
    private long totalItems;

    /**
     * Reset API: Response page number for pagination
     */
    private int page;

    /**
     * Reset API: Response size of one page for pagination
     */
    private int size;

    /**
     * Reset API: Response data of 1 item
     */
    private E item;

    /**
     * Reset API: Response data of list items
     */
    private List<E> items;

    /**
     * Reset API: Response message from service
     */
    private String serviceMessage;

    public ResponseBodyDto(E item,ResponseCode code,String message){
        this.code=code;
        this.message=message;
        this.item=item;
    }

    public ResponseBodyDto(List<E> items,ResponseCode code,String message,int totalItems){
        this.code=code;
        this.message=message;
        this.items=items;
        this.totalItems =totalItems ;
    }
    public ResponseBodyDto(List<E> items,ResponseCode code,String message,Long totalItems,int page, int size){
        this.code=code;
        this.message=message;
        this.items=items;
        this.totalItems =totalItems ;
        this.page = page;
        this.size = size;
    }

    public ResponseBodyDto(List<E> items, ResponseCode code, String message, PagingDto pagingDto){
        this.code=code;
        this.message=message;
        this.items=items;
        this.totalItems = pagingDto.getTotalItems() ;
        this.page = pagingDto.getPage();
        this.size = pagingDto.getSize();
    }

    public ResponseBodyDto(ResponseCode code,String message){
        this.code=code;
        this.message=message;
    }

    public ResponseBodyDto(String serviceMessage, ResponseCode code,String message){
        this.serviceMessage = serviceMessage;
        this.code=code;
        this.message=message;
    }
}
