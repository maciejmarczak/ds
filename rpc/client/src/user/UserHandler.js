const readlineSync = require('readline-sync');

const optsGenerator = (clazz, role) => {

    let userOpts = [
        { strVal: 'Exit', func: () => process.exit() }
    ];

    let workerOpts = [
        { strVal: 'Show medical exams', func: () => clazz.showMedicalExams() },
        { strVal: 'List all patients', func: () => clazz.listPatients() }
    ].concat(userOpts);

    let opts = {
        'DOCTOR': workerOpts,
        'PATIENT': null,
        'TECHNICIAN': workerOpts
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
        }
    }

    _showMedicalExams(id) {
        console.log('Showing medical exams of user ' + id);
        this.showMenu();
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
                console.log('ID:\t' + p.user.id + '\tNAME:\t' + p.user.full_name);
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
}

export class PatientHandler extends UserHandler {
    menuOptions = optsGenerator(this, 'PATIENT');

    showMedicalExams() {
        UserHandler._showMedicalExams(this.user.id);
    }
}
