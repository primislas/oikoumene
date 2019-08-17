export type TextValue = SearchedValue<string>;
export type NumberValue = SearchedValue<number>;

export class SearchedValue<T> {
    value: T;

    constructor(value: T = undefined) {
        this.value = value;
    }
}
