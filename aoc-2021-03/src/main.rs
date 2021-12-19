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
    oxygen: String,
    co2: String,
}

impl DiagnosticReport {
    pub fn new(input: &Vec<String>) -> Self {
        let mut new_self = Self {
            data: DiagnosticReport::convert_strvec_to_array2d(input),
            gamma: String::new(),
            epsilon: String::new(),
            oxygen: String::new(),
            co2: String::new(),
        };
        new_self.calculate_gamma();
        new_self.calculate_epsilon();
        new_self.calculate_oxygen();
        new_self.calculate_co2();

        new_self
    }

    pub fn get_gamma(&self) -> usize {
        usize::from_str_radix(&self.gamma, 2).expect("Can't convert gamma string to a binary number.")
    }
    pub fn get_epsilon(&self) -> usize {
        usize::from_str_radix(&self.epsilon, 2).expect("Can't convert epsilong string to a binary number.")
    }
    pub fn get_oxygen(&self) -> usize {
        usize::from_str_radix(&self.oxygen, 2).expect("Can't convert oxygen rate to a binary number.")
    }
    pub fn get_co2(&self) -> usize {
        usize::from_str_radix(&self.co2, 2).expect("Can't convert co2 scrubber rating to a binary number.")
    }

    fn convert_strvec_to_array2d(strvec: &Vec<String>) -> Array2D<char> {
        let mut vecvecchars: Vec<Vec<char>> = vec![];
        for an_str in strvec {
            if !an_str.chars().all(|c| c.is_whitespace()) {
                vecvecchars.push(an_str.chars().collect::<Vec<char>>());
            }
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

    fn calculate_oxygen(&mut self) {
        let mut new_oxygen = String::new();
        let mut filtered_data = self.data.clone();
        for column_index in 0..filtered_data.row_len() {
            let mut zeros_count = 0 as usize;
            let mut ones_count = 0 as usize;
            for row_index in 0..filtered_data.column_len() {
                match filtered_data[(row_index, column_index)] {
                    '0' => zeros_count += 1,
                    '1' => ones_count += 1,
                    _ => panic!("ERROR: input has some other character but 1 or 0."),
                }
            }
            match ones_count.cmp(&zeros_count) {
                Ordering::Less => Self::preserve_zeros_at_pos(&mut filtered_data, column_index),
                Ordering::Greater => Self::preserve_ones_at_pos(&mut filtered_data, column_index),
                Ordering::Equal => {
                    new_oxygen = Self::preserve_the_one_at_pos(&filtered_data, column_index);
                },
            }
        }

        self.oxygen = new_oxygen;
    }

    fn calculate_co2(&mut self) {
        let mut new_co2 = String::new();
        let mut filtered_data = self.data.clone();
        for column_index in 0..filtered_data.row_len() - 1 {
            let mut zeros_count = 0 as usize;
            let mut ones_count = 0 as usize;
            for row_index in 0..filtered_data.column_len() {
                match filtered_data[(row_index, column_index)] {
                    '0' => zeros_count += 1,
                    '1' => ones_count += 1,
                    _ => panic!("ERROR: input has some other character but 1 or 0."),
                }
            }
            match ones_count.cmp(&zeros_count) {
                Ordering::Less => Self::preserve_ones_at_pos(&mut filtered_data, column_index),
                Ordering::Greater => Self::preserve_zeros_at_pos(&mut filtered_data, column_index),
                Ordering::Equal => {
                    new_co2 = Self::preserve_the_zero_at_pos(&filtered_data, column_index);
                },
            }
        }

        self.co2 = new_co2;
    }

    fn preserve_zeros_at_pos(data: &mut Array2D<char>, pos: usize) {
        let mut new_data: Vec<Vec<char>> = vec![];
        for (row_index, _row) in data.rows_iter().enumerate() {
            if data[(row_index, pos)] == '0' {
                new_data.push(Self::row_to_vec_at_index(&data, row_index));
            }
        }
        *data = Self::vecvec_to_array2d(&new_data);
    }
    fn preserve_ones_at_pos(data: &mut Array2D<char>, pos: usize) {
        let mut new_data: Vec<Vec<char>> = vec![];
        for (row_index, _row) in data.rows_iter().enumerate() {
            if data[(row_index, pos)] == '1' {
                new_data.push(Self::row_to_vec_at_index(&data, row_index));
            }
        }
        *data = Self::vecvec_to_array2d(&new_data);
    }
    fn preserve_the_one_at_pos(data: &Array2D<char>, index: usize) -> String {
        if data.column_len() != 2 {
            panic!("The number of rows is different from 2 on an iteration where there are two items left in binary search. Something went wrong.");
        }
        let mut preserved = String::new();

        let preserved_row_index: usize = if data[(0,index)] == '1' { 0 } else { 1 };
        for (column_index, _column) in data.columns_iter().enumerate() {
            preserved.push(data[(preserved_row_index,column_index)]);
        }

        preserved
    }
    fn preserve_the_zero_at_pos(data: &Array2D<char>, index: usize) -> String {
        if data.column_len() != 2 {
            panic!("The number of rows is different from 2 on an iteration where there are two items left in binary search. Something went wrong.");
        }
        let mut preserved = String::new();

        let preserved_row_index: usize = if data[(0,index)] == '0' { 0 } else { 1 };
        for (column_index, _column) in data.columns_iter().enumerate() {
            preserved.push(data[(preserved_row_index,column_index)]);
        }

        preserved
    }

    fn row_to_vec_at_index(data: &Array2D<char>, row_index: usize) -> Vec<char> {
        let mut row_vec: Vec<char> = vec![];
        for (column_index, _column) in data.columns_iter().enumerate() {
            row_vec.push(data[(row_index, column_index)]);
        }

        row_vec
    }
    fn vecvec_to_array2d(vecvec: &Vec<Vec<char>>) -> Array2D<char> {
        Array2D::from_rows(vecvec.as_slice())
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

    let part2_answer = diag_data.get_oxygen() * diag_data.get_co2();
    println!("Part 2 answer is: {}.", part2_answer);
}