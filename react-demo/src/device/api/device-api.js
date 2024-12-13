import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    device: '/device'
};

function getDevices(callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.device, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}



function getDeviceByPersonId(params, callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.device + params.id, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function deleteDevice(params, callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.device + '/' + params.id, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json'
        }
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function updateDevice(user, callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.device + '/' + user.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(user)
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postDevice(user, callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.device, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

export {
    getDevices,
    getDeviceByPersonId,
    postDevice,
    deleteDevice,
    updateDevice
};
