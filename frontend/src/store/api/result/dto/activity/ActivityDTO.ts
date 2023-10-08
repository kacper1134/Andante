import { Page } from "../../Page";

export enum Priority {
    HIGHEST = "Highest",
    HIGH = "High",
    MEDIUM = "Medium",
    LOW = "Low",
    LOWEST = "Lowest",
};

export enum Domain {
    PRODUCT = "Product",
    ORDER = "Order",
    FORUM = "Forum",
};

export interface ActivityDTO {
    id: string,
    priority: Priority,
    domain: Domain,
    relatedId: string,
    affectedUsers: string[],
    description: string,
    acknowledgedUsers: string[],
    eventTimestamp: string,
};

export function isActivityDTO(value: any): value is ActivityDTO {
    return "priority" in value && "domain" in value; 
}

export function isActivityDTOPage(value: any): value is Page<ActivityDTO> {
    return "content" in value;
}