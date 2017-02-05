import { MyApp } from "../src/MyApp";

describe("areStringsEqual", () => {
    it("should return true if two strings are the same", () => {
        var result =
            new MyApp
            .MyStringUtils("whatever", "whoever")
            .areStringsEqual("t", "t");

        expect(result).toEqual(true);
    });
});