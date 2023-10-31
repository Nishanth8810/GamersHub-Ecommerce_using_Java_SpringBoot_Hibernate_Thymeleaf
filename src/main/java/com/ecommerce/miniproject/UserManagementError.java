package com.ecommerce.miniproject;

import lombok.Getter;


@Getter
    public enum UserManagementError {
        USER_ASSOCIATED_WITH_ORDER("Cannot delete this user because this user is associated with some order, try to block user")
        ;
        private final String errorMessage;

        UserManagementError(String errorMessage) {
            this.errorMessage = errorMessage;
        }

}


