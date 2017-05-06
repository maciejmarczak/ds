package org.maciejmarczak.ds.rpc.server.service;

import io.grpc.stub.StreamObserver;
import org.maciejmarczak.ds.rpc.server.dao.ExamDao;
import org.maciejmarczak.ds.rpc.server.dao.UserDao;
import org.maciejmarczak.ds.rpc.server.protos.*;

import java.util.List;

public class PatientService extends
        PatientServiceGrpc.PatientServiceImplBase {

    private final UserDao userDao = new UserDao();
    private final ExamDao examDao = new ExamDao();

    @Override
    public void getExamsByPatientId(PatientServiceOuterClass.PatientId request,
                                    StreamObserver<ExamList> responseObserver) {

        List<Exam> exams = examDao.getExamsByPatientId(request.getId());

        responseObserver.onNext(toExamListMsg(exams));
        responseObserver.onCompleted();
    }

    private ExamList toExamListMsg(List<Exam> exams) {
        return ExamList.newBuilder()
                .addAllExam(exams)
                .build();
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
