package com.whatacook.cookers.view;

public class UserServiceBuilder {
    private ServiceComponentToFind service;
    private ServiceComponentToLogin login;
    private ServiceComponentToSave save;

    public UserServiceBuilder setService(ServiceComponentToFind service) {
        this.service = service;
        return this;
    }

    public UserServiceBuilder setLogin(ServiceComponentToLogin login) {
        this.login = login;
        return this;
    }

    public UserServiceBuilder setSave(ServiceComponentToSave save) {
        this.save = save;
        return this;
    }

    public UserService createUserService() {
        return new UserService(service, login, save);
    }
}