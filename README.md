# junit4-test-finder

Finds JUnit4 test runner aware tests at compile time.

## Usage

1. Implement `com.github.old_horizon.junit4.JUnit4TestFoundListener`.
1. Create jar including `META-INF/services/com.github.old_horizon.junit4.JUnit4TestFoundListener`.
    * Listeners specified in this file will be invoked.
1. Add the following jars to compile-time classpath: this and the listener you created above.
1. Compile source code.

## License

MIT
