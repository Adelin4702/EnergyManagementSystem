import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";
import { jwtDecode } from 'jwt-decode';

const endpoint = {
    myDevice: '/device/myDevice'
};

function getDeviceByPersonId(params, callback){
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api_device + endpoint.myDevice + '/' +  params, {
       method: 'GET',
       headers:{
        'Authorization': `Bearer ${jwt}`
        }
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getDeviceByPersonId,
}