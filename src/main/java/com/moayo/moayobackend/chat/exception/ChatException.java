package com.moayo.moayobackend.chat.exception;

import com.moayo.moayobackend.chat.exception.code.ChatErrorCode;
import com.moayo.moayobackend.global.exception.BusinessException;

public class ChatException extends BusinessException {
    public ChatException(ChatErrorCode errorCode) {
      super(errorCode);
    }

}
