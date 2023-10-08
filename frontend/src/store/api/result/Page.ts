export interface Page<T> {
    content: T[],
    pageable: {
        sort: {
            unsorted: boolean,
            sorted: boolean,
            empty: boolean,
        },
        pageSize: number,
        pageNumber: number,
        offset: number,
        unpaged: boolean,
        paged: boolean,
    },
    totalPages: number,
    totalElements: number,
    last: boolean,
    first: boolean,
    numberOfElements: number,
    sort: {
        unsorted: boolean,
        sorted: boolean,
        empty: boolean,
    },
    number: number,
    size: number,
    empty: boolean,
};

export function getEmptyPage<T>(): Page<T> {
    return {
        content: [],
        pageable: {
            sort: {
                unsorted: false,
                sorted: false,
                empty: true,
            },
            pageSize: 0,
            pageNumber: 0,
            offset: 0,
            unpaged: false,
            paged: false,
        },
        totalPages: 0,
        totalElements: 0,
        last: false,
        first: false,
        numberOfElements: 0,
        sort: {
            unsorted: false,
            sorted: false,
            empty: false,
        },
        number: 0,
        size: 0,
        empty: false,
    };
}