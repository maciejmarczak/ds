package org.maciejmarczak.ds.rpc.server.service;

import io.grpc.stub.StreamObserver;
import org.maciejmarczak.ds.rpc.server.dao.UserDao;
import org.maciejmarczak.ds.rpc.server.protos.*;

import java.util.List;

public class PatientService extends
        PatientServiceGrpc.PatientServiceImplBase {

    private final UserDao userDao = new UserDao();

    @Override
    public void getAllPatients(EmptyOuterClass.Empty request,
                               StreamObserver<PatientServiceOuterClass.PatientList> responseObserver) {

        List<User> users = userDao.getAllPatients();

        responseObserver.onNext(toPatientListMsg(users));
        responseObserver.onCompleted();
    }

    private PatientServiceOuterClass.PatientList toPatientListMsg(List<User> patients) {
        return PatientServiceOuterClass.PatientList.newBuilder()
                .addAllPatient(patients)
                .build();
    }
}
