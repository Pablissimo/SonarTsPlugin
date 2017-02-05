module.exports = function (config) {
    config.set({
        frameworks: ["jasmine", "karma-typescript"],
        files: [
            { pattern: "src/**/*.ts" },
            { pattern: "test/**/*.ts" }
        ],
        preprocessors: {
            "**/*.ts": ["karma-typescript"]
        },
        reporters: ["progress", "karma-typescript"],
        browsers: ["PhantomJS"],

        karmaTypescriptConfig: {
            reports: {
                "lcovonly": {
                    directory: "coverage",
                    subdirectory: ".",
                    filename: "lcov.info"
                }
            },
            tsconfig: "./tsconfig.json"
        }
    });
};