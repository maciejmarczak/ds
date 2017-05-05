const fs = require('fs');
const grpc = require('grpc');

class ProtosLoader {
    path;

    constructor(path) {
        this.path = path;
    }

    loadProtos() {
        let protos = fs.readdirSync(this.path)
            .filter(p => p.endsWith('.proto'))
            .map(ProtosLoader.parseProtoDesc);

        let protoObj = {};

        for (let proto of protos) {
            protoObj[proto.objName] = grpc.load(this.path +
                proto.name)[proto.objName];
        }

        return protoObj;
    }

    static parseProtoDesc(name) {
        let objName = name
            .split('-')
            .map(capitalize)
            .join('');

        objName = removeExt(objName);

        function capitalize(value) {
            return value.charAt(0).toUpperCase() +
                value.slice(1).toLowerCase();
        }

        function removeExt(value) {
            return value.slice(0, - '.proto'.length);
        }

        return { name: name, objName: objName };
    }
}

export default ProtosLoader;