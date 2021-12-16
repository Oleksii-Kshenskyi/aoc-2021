use std::io::BufRead;
use array2d::Array2D;
use std::cmp::Ordering;

static NO_INPUT_FILE: &str = "ERROR: file name with the input data not specified!\nRun again with the file name as the first argument for this application.";
static CANT_OPEN_FILE: &str = "ERROR: The file specified cannot be opened.";

#[derive(Debug)]
struct DiagnosticReport {
    data: Array2D<char>,
    gamma: String,
    epsilon: String,
}

impl DiagnosticReport {
    pub fn new(input: &Vec<String>) -> Self {
        let mut new_self = Self {
            data: DiagnosticReport::convert_strvec_to_array2d(input),
            gamma: String::new(),
            epsilon: String::new(),
        };
        new_self.calculate_gamma();
        new_self.calculate_epsilon();

        new_self
    }

    pub fn get_gamma(&self) -> usize {
        usize::from_str_radix(&self.gamma, 2).expect("Can't convert gamma string to a binary number.")
    }
    pub fn get_epsilon(&self) -> usize {
        usize::from_str_radix(&self.epsilon, 2).expect("Can't convert epsilong string to a binary number.")
    }

    fn convert_strvec_to_array2d(strvec: &Vec<String>) -> Array2D<char> {
        let mut vecvecchars: Vec<Vec<char>> = vec![];
        for an_str in strvec {
            vecvecchars.push(an_str.chars().collect::<Vec<char>>());
        }

        Array2D::from_rows(vecvecchars.as_slice())
    }

    fn calculate_gamma(&mut self) {
        let mut new_gamma = String::new();
        for column in self.data.columns_iter() {
            let mut zeros_count = 0 as usize;
            let mut ones_count = 0 as usize;
            for element in column {
                match element {
                    '0' => zeros_count += 1,
                    '1' => ones_count += 1,
                    _ => panic!("ERROR: input has some other character but 1 or 0."),
                }
            }
            match ones_count.cmp(&zeros_count) {
                Ordering::Less => new_gamma.push('0'),
                Ordering::Greater => new_gamma.push('1'),
                Ordering::Equal => unreachable!("There shouldn't be the same amount of 0s and 1s in a single input line."),
            }
        }

        self.gamma = new_gamma;
    }

    fn calculate_epsilon(&mut self) {
        if self.gamma.is_empty() {
            panic!("Epsilon is calculated in terms of gamma, therefore gamma should be calculated first.");
        }

        let mut new_epsilon = String::new();
        for ch in self.gamma.chars() {
            match ch {
                '0' => new_epsilon.push('1'),
                '1' => new_epsilon.push('0'),
                _ => unreachable!("DiagnosticReport::calculate_epsilon: a gamma character is something other than 0 or 1."),
            }
        }

        self.epsilon = new_epsilon;
    }
}

fn main() {
    let filename: String = std::env::args()
        .collect::<Vec<String>>()
        .get(1)
        .expect(NO_INPUT_FILE)
        .clone();

    let input: Vec<String>;
    if let Ok(input_file) = std::fs::File::open(filename) {
        input = std::io::BufReader::new(input_file)
            .lines()
            .map(|line| line.unwrap() )
            .collect();
    } else {
        panic!("{}", CANT_OPEN_FILE);
    }

    let diag_data = DiagnosticReport::new(&input);
    let part1_answer = diag_data.get_gamma() * diag_data.get_epsilon();
    println!("Part 1 answer is: {}.", part1_answer);
}