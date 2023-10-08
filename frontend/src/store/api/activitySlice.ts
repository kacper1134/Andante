import { useToast } from "@chakra-ui/react";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { useEffect, useState } from "react";
import { RootState } from "..";
import { FilterOption } from "../../components/Notifications/Notifications";
import { ActivityDTO, isActivityDTOPage } from "./result/dto/activity/ActivityDTO";
import { getEmptyPage, Page } from "./result/Page";

const baseQuery = fetchBaseQuery({
    baseUrl: "http://localhost:4561",
    credentials: "include",
    prepareHeaders: (headers, { getState }) => {
        const token = (getState() as RootState).auth.idToken;

        if (token) {
            headers.set("Authorization", `Bearer ${token}`);
        }

        return headers;
    }
});

const activitySlice = createApi({
    reducerPath: "activity",
    baseQuery: baseQuery,
    endpoints: (builder) => ({
        getByUser: builder.query<Page<ActivityDTO> | string[], {email: string, page: number, count: number}>({
            query: ({email, page, count}) => ({
                url: `/activity/user/${email}?page=${page}&size=${count}`,
            })
        }),
        getUnreadByUser: builder.query<Page<ActivityDTO> | string[], {email: string, page: number, count: number}>({
            query: ({email, page, count}) => ({
                url : `/activity/user/unread/${email}?page=${page}&size=${count}`,
            }),
        }),
        getReadByUser: builder.query<Page<ActivityDTO> | string[], {email: string, page: number, count: number}>({
            query: ({email, page, count}) => ({
                url: `/activity/user/acknowledged/${email}?page=${page}&size=${count}`
            }),
        }),
        getAffectingAll: builder.query<Page<ActivityDTO> | string[], {page: number, count: number}>({
            query: ({page, count}) => ({
                url: `/activity/general?page=${page}&size=${count}`,
            }),
        }),
        markAsRead: builder.mutation<void, {id: string, email: string}>({
            query: ({id, email}) => ({
                url: `/activity/viewed?id=${id}&email=${email}`,
                method: "POST",
            }),
        }),
    }),
});

export const { useLazyGetByUserQuery, useGetByUserQuery, useGetUnreadByUserQuery, useGetReadByUserQuery, useLazyGetReadByUserQuery, useLazyGetUnreadByUserQuery, useLazyGetAffectingAllQuery, useMarkAsReadMutation } = activitySlice;

export function useGetAffectingAll(email: string, page: number, count: number): Page<ActivityDTO> {
    const toast = useToast();
    const [trigger] = useLazyGetAffectingAllQuery();
    const [activities, setActivities] = useState<Page<ActivityDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch activities from our service";

    useEffect(() => {
        const fetchActivities = async () => {
            const response = await trigger({page: page, count: count});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isActivityDTOPage(response.data)) {
                setActivities(response.data);
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchActivities().catch(() => setErrorMessages([generalErrorMessage]));
    }, [trigger, page, count]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

     return activities;
}

export function useGetByUser(email: string, page: number, count: number): Page<ActivityDTO> {
    const toast = useToast();
    const [trigger] = useLazyGetByUserQuery();
    const [activities, setActivities] = useState<Page<ActivityDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch activities from our service";

    useEffect(() => {
        const fetchActivities = async () => {
            const response = await trigger({email: email, page: page, count: count});

            if (response.data) {
                if (isActivityDTOPage(response.data)) {
                    setActivities(response.data);
                } else {
                    setErrorMessages(response.data);
                }
            }
        }

        fetchActivities().catch(() => setErrorMessages([generalErrorMessage]));
    }, [trigger, email, page, count]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

     return activities;
}

export function useGetReadByUser(email: string, page: number, count: number): Page<ActivityDTO> {
    const toast = useToast();
    const [trigger] = useLazyGetReadByUserQuery();
    const [activities, setActivities] = useState<Page<ActivityDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch read activities from our service";

    useEffect(() => {
        const fetchActivities = async () => {
            const response = await trigger({email: email, page: page, count: count});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isActivityDTOPage(response.data)) {
                setActivities(response.data);
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchActivities().catch(() => setErrorMessages([generalErrorMessage]));
    }, [trigger, email, page, count]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

     return activities;
}

export function useGetUnreadByUser(email: string, page: number, count: number): Page<ActivityDTO> {
    const toast = useToast();
    const [trigger] = useLazyGetUnreadByUserQuery();
    const [activities, setActivities] = useState<Page<ActivityDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch unread activities from our service";

    useEffect(() => {
        const fetchActivities = async () => {
            const response = await trigger({email: email, page: page, count: count});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isActivityDTOPage(response.data)) {
                setActivities(response.data);
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchActivities().catch(() => setErrorMessages([generalErrorMessage]));
    }, [trigger, email, page, count]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

     return activities;
}

export function useGetByUserAndFilterOption(email: string , page: number, count: number, filterOption: FilterOption): Page<ActivityDTO> {
    let hookToCall: (email: string, page: number, count: number) => Page<ActivityDTO>;
    
    switch (filterOption) {
        case FilterOption.ALL:
            hookToCall = useGetByUser;
            break;
        case FilterOption.READ:
            hookToCall = useGetReadByUser;
            break;
        case FilterOption.UNREAD:
            hookToCall = useGetUnreadByUser;            
        }


    return hookToCall(email, page, count);
}

export default activitySlice;