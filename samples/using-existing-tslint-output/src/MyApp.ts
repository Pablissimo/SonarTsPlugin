export module MyApp {
    /*
        Contains a couple of methods that our test class will call, and
        some awful code that will cause tslint to flag a couple of basic
        issues
    */
    export class MyStringUtils {
        constructor(public dummy1: string, public dummy2: string) {
        }

        public areStringsEqual(s1: string, s2: string) {
            if (s1 == s2) {
                return true;
            }
            else
                return false;
        }
    }

    /*
        Contains a method that none of the test code hits, and
        that tslint will flag another couple of issues on
    */
    export class MyUncoveredClass {
        public justReturn42(param1:string,param2: number) {
            return 42;
        }

    }
}