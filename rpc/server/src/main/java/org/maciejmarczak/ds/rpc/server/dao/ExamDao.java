package org.maciejmarczak.ds.rpc.server.dao;

import org.maciejmarczak.ds.rpc.server.protos.Exam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExamDao {

    public List<Exam> getExamsByPatientId(String patientId) {
        return ExamMockData.EXAMS.stream()
                .filter(e -> e.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    static class ExamMockData {

        static final Random RAND = new Random();

        static final List<Exam.ParamGroup> PARAM_GROUPS = Arrays.asList(
                createParamGroup("Biochemia", createParams("ALT,13,U/I", "AST,19,U/I", "Potas,3.81,mmol/l")),
                createParamGroup("Immunochemia", createParams("WAG,83,%", "UMZ,23,I/I", "IMC,89,%")),
                createParamGroup("Analityka", createParams("UMP,12,ug", "AWZ,99,%")),
                createParamGroup("Hematologia", createParams("INT,64,k"))
        );

        static final List<Exam> EXAMS = Arrays.asList(
                createExam("101", "100", getDate("22-07-2016"), getRandomParamGroups()),
                createExam("102", "100", getDate("11-05-2015"), getRandomParamGroups()),
                createExam("101", "100", getDate("26-03-2016"), getRandomParamGroups())
        );

        static Exam createExam(String patientId, String doctorId,
                               long date, List<Exam.ParamGroup> paramGroups) {
            return Exam.newBuilder()
                    .setPatientId(patientId)
                    .setDoctorId(doctorId)
                    .setDate(date)
                    .addAllParamGroups(paramGroups)
                    .build();
        }

        static long getDate(String date) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            long time = 0;

            try {
                Date d = formatter.parse(date);
                time = d.getTime();
            } catch (ParseException ignored) {}

            return time;
        }

        static List<Exam.ParamGroup.Param> createParams(String... params) {
            List<Exam.ParamGroup.Param> results = new LinkedList<>();

            for (String param : params) {
                String[] splitted = param.split(",");
                Exam.ParamGroup.Param p = Exam.ParamGroup.Param.newBuilder()
                        .setName(splitted[0])
                        .setValue(splitted[1])
                        .setUnit(splitted[2])
                        .build();

                results.add(p);
            }

            return results;
        }

        static Exam.ParamGroup createParamGroup(String name, List<Exam.ParamGroup.Param> params) {
            return Exam.ParamGroup.newBuilder()
                    .setName(name)
                    .addAllParams(params)
                    .build();
        }

        static List<Exam.ParamGroup> getRandomParamGroups() {
            int maxIdx = PARAM_GROUPS.size() - 1;

            int lowerLimit = RAND.nextInt(maxIdx);
            int upperLimit = maxIdx - RAND.nextInt(maxIdx - lowerLimit);

            return PARAM_GROUPS.subList(lowerLimit, upperLimit);
        }
    }
}
