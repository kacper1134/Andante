import { DateTime } from "luxon";
import { UserDetails } from "../../../../../utils/KeycloakUtils";

export interface UserRepresentation {
    id: string,
    username: string,
    firstName: string,
    lastName: string,
    email: string,
    emailVerified: boolean,
    attributes: {
        country: string[],
        gender: string[],
        city: string[],
        street: string[],
        postalcode: string[],
        phonenumber: string[],
        description: string[],
        paymentMethod: string[],
        dateOfBirth: string[],
        currency: string[],
    }
};

export function isUserRepresentation(value: any): value is UserRepresentation {
    return "attributes" in value && "id" in value;
}

export function toUserDetails(userRepresentation: UserRepresentation): UserDetails {
    return {
        delivery: {
            country: userRepresentation.attributes.country.length > 0 ? userRepresentation.attributes.country[0] : "Not provided",
            city: userRepresentation.attributes.city.length > 0 ? userRepresentation.attributes.city[0] : "Not provided",
            street: userRepresentation.attributes.street.length > 0 ? userRepresentation.attributes.street[0] : "Not provided",
            postalCode: userRepresentation.attributes.postalcode.length > 0 ? userRepresentation.attributes.postalcode[0] : "Not provided",
        },
        personal: {
            accountCreated: DateTime.now(),
            dateOfBirth: DateTime.fromISO(userRepresentation.attributes.dateOfBirth[0]),
            emailAddress: userRepresentation.email,
            name: userRepresentation.firstName,
            surname: userRepresentation.lastName,
            username: userRepresentation.username,
            gender: userRepresentation.attributes.gender.length > 0  ? userRepresentation.attributes.gender[0] : "Unknown",
            phoneNumber: userRepresentation.attributes.phonenumber.length > 0 ? userRepresentation.attributes.phonenumber[0] : "Unknown",
            description: userRepresentation.attributes.description.length > 0 ? userRepresentation.attributes.description[0] : "Not provided",
        }
    }
};