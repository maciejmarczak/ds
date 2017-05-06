package org.maciejmarczak.ds.rpc.server.service;

import io.grpc.stub.StreamObserver;
import org.maciejmarczak.ds.rpc.server.dao.UserDao;
import org.maciejmarczak.ds.rpc.server.protos.*;

import java.util.List;
import java.util.stream.Collectors;

public class PatientService extends
        PatientServiceGrpc.PatientServiceImplBase {

    private final UserDao userDao = new UserDao();

    @Override
    public void getAllPatients(EmptyOuterClass.Empty request,
                               StreamObserver<PatientServiceOuterClass.PatientList> responseObserver) {

        List<User> users = userDao.getAllPatients();

        List<Patient> patients = users.stream()
                .map(this::createFromUser)
                .collect(Collectors.toList());

        responseObserver.onNext(toPatientListMsg(patients));
        responseObserver.onCompleted();
    }

    private Patient createFromUser(User user) {
        return Patient.newBuilder()
                .setUser(user)
                .build();
    }

    private PatientServiceOuterClass.PatientList toPatientListMsg(List<Patient> patients) {
        return PatientServiceOuterClass.PatientList.newBuilder()
                .addAllPatient(patients)
                .build();
    }
}
