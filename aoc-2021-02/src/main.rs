use std::io::BufRead;

static NO_INPUT_FILE: &str = "ERROR: file name with the input data not specified!\nRun again with the file name as the first argument for this application.";
static CANT_OPEN_FILE: &str = "ERROR: The file specified cannot be opened.";
static COMMAND_INCORRECT: &str = "Check your input file, at least one of commands in the list is incorrect.";

pub enum Direction {
    Up,
    Down,
    Forward,
}

pub struct Command {
    pub direction: Direction,
    pub argument: usize,
}

impl Command {
    pub fn new() -> Self {
        Self {direction: Direction::Up, argument: 0}
    }
}

struct Commands {
    pub commands_vec: Vec<Command>
}

impl Commands {
    fn new(input: Vec<String>) -> Self {
        let mut new_commands: Vec<Command> = vec![];
        for line in input {
            let command_lines: Vec<&str> = line.split(" ").collect();
            let mut the_command = Command::new();

            the_command.direction = match command_lines[0] {
                "up" => Direction::Up,
                "forward" => Direction::Forward,
                "down" => Direction::Down,
                _ => panic!("{}", COMMAND_INCORRECT),
            };

            the_command.argument = command_lines[1].parse::<usize>().expect(COMMAND_INCORRECT);

            new_commands.push(the_command);
        }

        Self {
            commands_vec: new_commands
        }
    }
}

#[derive(Clone, Copy)]
struct Submarine {
    horizontal: usize,
    depth: usize,
    aim: usize,
}

impl Submarine {
    pub fn new() -> Self {
        Self {
            horizontal: 0,
            depth: 0,
            aim: 0,
        }
    }

    pub fn from_commands_v1(commands: &Commands) -> Self {
        *Submarine::new().execute_v1(&commands)
    }
    pub fn from_commands_v2(commands: &Commands) -> Self {
        *Submarine::new().execute_v2(&commands)
    }

    pub fn calculate_pos_product(&self) -> usize {
        self.depth * self.horizontal
    }

    fn execute_v1(&mut self, commands: &Commands) -> &Self {
        for command in &commands.commands_vec {
            match command.direction {
                Direction::Up => self.depth = self.depth - command.argument,
                Direction::Down => self.depth = self.depth + command.argument,
                Direction::Forward => self.horizontal = self.horizontal + command.argument,
            }
        }

        self
    }
    fn execute_v2(&mut self, commands: &Commands) -> &Self {
        for command in &commands.commands_vec {
            match command.direction {
                Direction::Up => self.aim = self.aim - command.argument,
                Direction::Down => self.aim = self.aim + command.argument,
                Direction::Forward => {
                    self.horizontal = self.horizontal + command.argument;
                    self.depth += self.aim * command.argument;
                },
            }
        }

        self
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

    let commands = Commands::new(input);

    let part1_answer = Submarine::from_commands_v1(&commands).calculate_pos_product();
    println!("Part 1 answer: {}.", part1_answer);

    let part2_answer = Submarine::from_commands_v2(&commands).calculate_pos_product();
    println!("Part 2 answer: {}.", part2_answer);
}