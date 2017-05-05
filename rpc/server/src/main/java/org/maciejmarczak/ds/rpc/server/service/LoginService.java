package org.maciejmarczak.ds.rpc.server.service;

import io.grpc.stub.StreamObserver;
import org.maciejmarczak.ds.rpc.server.dao.UserDao;
import org.maciejmarczak.ds.rpc.server.protos.*;

public class LoginService extends LoginServiceGrpc.LoginServiceImplBase {

    private final UserDao userDao = new UserDao();

    @Override
    public void login(LoginServiceOuterClass.LoginRequest request,
                      StreamObserver<User> responseObserver) {

        String userId = request.getId();
        responseObserver.onNext(userDao.getById(userId));
        responseObserver.onCompleted();
    }
}
