package org.maciejmarczak.ds.rpc.server.service;

import io.grpc.stub.StreamObserver;
import org.maciejmarczak.ds.rpc.server.dao.ExamDao;
import org.maciejmarczak.ds.rpc.server.dao.UserDao;
import org.maciejmarczak.ds.rpc.server.protos.*;

import java.util.List;
import java.util.Random;

public class PatientService extends
        PatientServiceGrpc.PatientServiceImplBase {

    private final UserDao userDao = new UserDao();
    private final ExamDao examDao = new ExamDao();
    private final Random random = new Random();

    @Override
    public void getExamsByPatientId(PatientServiceOuterClass.PatientId request,
                                    StreamObserver<Exam> responseObserver) {

        List<Exam> exams = examDao.getExamsByPatientId(request.getId());

        // mock server delay
        for (Exam exam : exams) {
            try {
                Thread.sleep(random.nextInt(1500) + 1500);
                responseObserver.onNext(exam);
            } catch (InterruptedException ignored) {}
        }
        responseObserver.onCompleted();
    }

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
