use std::io::BufRead;

static NO_INPUT_FILE: &str = "ERROR: file name with the input data not specified!\nRun again with the file name as the first argument for this application.";
static CANT_OPEN_FILE: &str = "ERROR: The file specified cannot be opened.";
static CANT_CONVERT_TO_INT: &str = "ERROR: One of the lines in the file cannot be convert to an unsigned integer. Check that your input it correct and rerun.";

fn main() {
    let filename: String = std::env::args()
        .collect::<Vec<String>>()
        .get(1)
        .expect(NO_INPUT_FILE)
        .clone();

    let input: Vec<u32>;
    if let Ok(input_file) = std::fs::File::open(filename) {
        input = std::io::BufReader::new(input_file)
            .lines()
            .map(|the_str| the_str.unwrap().parse::<u32>().expect(CANT_CONVERT_TO_INT))
            .collect::<Vec<u32>>();
    } else {
        panic!("{}", CANT_OPEN_FILE);
    }
}