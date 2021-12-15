use std::io::BufRead;

static NO_INPUT_FILE: &str = "ERROR: file name with the input data not specified!\nRun again with the file name as the first argument for this application.";
static CANT_OPEN_FILE: &str = "ERROR: The file specified cannot be opened.";
static CANT_CONVERT_TO_INT: &str = "ERROR: One of the lines in the file cannot be convert to an unsigned integer. Check that your input it correct and rerun.";

fn calculate_increases(input: &Vec<u32>) -> u32 {
    let mut previous = std::u32::MAX;
    let mut part1_answer: u32 = 0;
    for (i, val) in input.iter().enumerate() {
        if i != 0 && *val > previous {
            part1_answer += 1;
        }
        previous = *val;
    }

    part1_answer
}

fn calculate_three_measurements(input: &Vec<u32>) -> Vec<u32> {
    let mut new_input = vec![];

    for i in 0..(input.len() - 2) {
        new_input.push(input[i] + input[i + 1] + input[i + 2]);
    }

    new_input
}

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

    let part1_answer = calculate_increases(&input);
    println!("Answer to part 1 is: {}.", part1_answer);

    let part2_input = calculate_three_measurements(&input);
    let part2_answer = calculate_increases(&part2_input);
    println!("Answer to part 2 is: {}.", part2_answer);
}
