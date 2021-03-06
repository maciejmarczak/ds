const readlineSync = require('readline-sync');
const grpc = require('grpc');

const optsGenerator = (clazz, role) => {

    let userOpts = [
        { strVal: 'Show medical exams', func: () => clazz.showMedicalExams() }
    ];

    let workerOpts = [
        { strVal: 'List all patients', func: () => clazz.listPatients() }
    ].concat(userOpts);

    let technicianOpts = [
        { strVal: 'Add new exam', func: () => clazz.addExam() }
    ].concat(workerOpts);

    let opts = {
        'DOCTOR': workerOpts,
        'PATIENT': userOpts,
        'TECHNICIAN': technicianOpts
    };

    return opts[role];
};

class UserHandler {
    menuOptions;
    user;

    constructor(user) {
        this.user = user;
    }

    showMenu() {
        let mappedOptions = this.menuOptions.map(o => o.strVal);
        let idx = readlineSync.keyInSelect(mappedOptions, 'Which action?');

        let choice = this.menuOptions[idx];

        if (choice) {
            choice.func();
        } else {
            grpc.getClientChannel(client).close();
            grpc.getClientChannel(patient).close();
        }
    }

    _showMedicalExams(id) {
        console.log('\nMEDICAL EXAMS OF PATIENT ' + id);
        let call = patient.getExamsByPatientId({ id: id });

        call.on('data', printExam);
        call.on('end', () => this.showMenu());

        function printExam(exam) {
            console.log('\n\nDoctorId:\t' + exam.doctorId + '\tDate:\t' +
                new Date(parseInt(exam.date)).toDateString());

            exam.paramGroups.forEach(printParamGroup);

            function printParamGroup(paramGroup) {
                console.log(paramGroup.name);
                for (let param of paramGroup.params) {
                    console.log('\t\t' + param.name + '\t' + param.value +
                        '\t' + param.unit);
                }
            }
        }
    }
}

class WorkerHandler extends UserHandler {
    showMedicalExams() {
        let id = readlineSync.question('Patient id: ');
        this._showMedicalExams(id);
    }

    listPatients() {
        patient.getAllPatients({}, (err, response) => {
            let patientArray = response.patient;
            patientArray.forEach(p => {
                console.log('ID:\t' + p.id + '\tNAME:\t' + p.full_name);
            });
            this.showMenu();
        });
    }
}

export class DoctorHandler extends WorkerHandler {
    menuOptions = optsGenerator(this, 'DOCTOR');
}

export class TechnicianHandler extends WorkerHandler {
    menuOptions = optsGenerator(this, 'TECHNICIAN');

    addExam() {
        let patientId = readlineSync.question('Patient Id: ');
        let doctorId = readlineSync.question('Doctor Id: ');
        let date = new Date().getTime();

        let exam = {
            patientId: patientId,
            doctorId: doctorId,
            date: date,
            paramGroups: []
        };

        let addNewParamGroup = true;
        while (addNewParamGroup) {
            let groupName = readlineSync.question('Group name: ');

            let paramGroup = { name: groupName, params: [] };

            let addNewParam = true;
            while (addNewParam) {
                let name = readlineSync.question('Param name: ');
                let value = readlineSync.question('Param value: ');
                let unit = readlineSync.question('Param unit: ');

                paramGroup.params.push({ name: name, value: value, unit: unit });

                addNewParam = readlineSync.keyInYN('Add new param?');
            }

            exam.paramGroups.push(paramGroup);

            addNewParamGroup = readlineSync.keyInYN('Add new param group?');
        }

        patient.addExam(exam, (err, response) => {
            console.log(response);
            this.showMenu();
        });
    }
}

export class PatientHandler extends UserHandler {
    menuOptions = optsGenerator(this, 'PATIENT');

    showMedicalExams() {
        this._showMedicalExams(this.user.id);
    }
}
